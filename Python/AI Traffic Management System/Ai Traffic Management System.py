import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import matplotlib.patches as patches
import numpy as np

# Load traffic data
data = pd.read_csv(r"traffic_data.csv")

# Initialize the plot with a grid layout
fig = plt.figure(figsize=(20, 12))
grid = plt.GridSpec(4, 4, figure=fig, hspace=0.8, wspace=0.8)  # Increased spacing

# Main simulation plot
ax_main = fig.add_subplot(grid[0:3, 0:2])  # Span 3 rows and 2 columns
ax_main.set_xlim(-1, 11)
ax_main.set_ylim(-1, 11)
ax_main.set_aspect('equal')
ax_main.axis('off')

# Define the roundabout
roundabout = patches.Circle((5, 5), 1, fill=False, color='black', linewidth=2)
ax_main.add_patch(roundabout)

# Add road markings (lanes and crosswalks)
for i in range(0, 11, 2):
    ax_main.plot([i, i], [0, 10], color='gray', linestyle='--', linewidth=0.5)  # Vertical lanes
    ax_main.plot([0, 10], [i, i], color='gray', linestyle='--', linewidth=0.5)  # Horizontal lanes

# Add crosswalks
ax_main.add_patch(patches.Rectangle((4.5, 0), 1, 1, color='white', alpha=0.5))  # South crosswalk
ax_main.add_patch(patches.Rectangle((4.5, 9), 1, 1, color='white', alpha=0.5))  # North crosswalk
ax_main.add_patch(patches.Rectangle((0, 4.5), 1, 1, color='white', alpha=0.5))  # West crosswalk
ax_main.add_patch(patches.Rectangle((9, 4.5), 1, 1, color='white', alpha=0.5))  # East crosswalk

class TrafficLight:
    """Represents a traffic light at an intersection."""
    def __init__(self, ax, x, y, facecolor):
        self.light = patches.Circle((x, y), 0.3, facecolor=facecolor, edgecolor='black', linewidth=1.5)
        ax.add_patch(self.light)
        self.timer = 0  # Timer for light duration

    def set_color(self, facecolor):
        """Update the color of the traffic light."""
        self.light.set_facecolor(facecolor)

class FarLight:
    """Represents a far light (e.g., warning light) for approaching vehicles."""
    def __init__(self, ax, x, y, facecolor):
        self.light = patches.Circle((x, y), 0.2, facecolor=facecolor, edgecolor='black', linewidth=1.5)
        ax.add_patch(self.light)

    def set_color(self, facecolor):
        """Update the color of the far light."""
        self.light.set_facecolor(facecolor)

# Initialize traffic lights and far lights
traffic_lights = {
    "North": TrafficLight(ax_main, 5, 9, 'red'),
    "East": TrafficLight(ax_main, 9, 5, 'red'),
    "South": TrafficLight(ax_main, 5, 1, 'red'),
    "West": TrafficLight(ax_main, 1, 5, 'red')
}

# Adjust far light positions to avoid cutting off
far_lights = {
    "North": FarLight(ax_main, 5, 10.5, 'yellow'),  # Moved further up
    "East": FarLight(ax_main, 10.5, 5, 'yellow'),   # Moved further right
    "South": FarLight(ax_main, 5, -0.5, 'yellow'),  # Moved further down
    "West": FarLight(ax_main, -0.5, 5, 'yellow')    # Moved further left
}

# Initialize vehicle data and transition timer
vehicle_data = {dir: {"approaching": 0, "waiting": 0} for dir in traffic_lights}
transition_timer = {"direction": None, "countdown": 0}

# Simulate moving vehicles
vehicles = []
emergency_vehicles = []

# Data for graphs
traffic_distribution = {dir: 0 for dir in traffic_lights}
waiting_times = {dir: [] for dir in traffic_lights}
time_steps = []
traffic_accuracy = []  # Placeholder for traffic flow accuracy
traffic_density = []  # Traffic density over time
average_waiting_time = {dir: [] for dir in traffic_lights}  # Average waiting time by direction
traffic_flow_efficiency = []  # Traffic flow efficiency over time

# Create subplots for graphs
ax_traffic_dist = fig.add_subplot(grid[0, 2])  # Traffic distribution by direction
ax_emergency = fig.add_subplot(grid[0, 3])  # Emergency vehicles pie chart
ax_waiting_time = fig.add_subplot(grid[1, 2])  # Waiting time over time
ax_traffic_density = fig.add_subplot(grid[1, 3])  # Traffic density over time
ax_avg_waiting_time = fig.add_subplot(grid[2, 2])  # Average waiting time by direction
ax_traffic_flow = fig.add_subplot(grid[2, 3])  # Traffic flow efficiency

def update_traffic_lights(direction, emergency):
    """Update traffic lights based on traffic and emergency conditions."""
    global transition_timer

    if emergency == "Yes":
        # Handle emergency: set the specified direction to green, others to red
        for dir, light in traffic_lights.items():
            light.set_color('green' if dir == direction else 'red')
        vehicle_data[direction]["waiting"] = 0
        return

    # Determine the direction with the highest traffic
    sorted_directions = sorted(
        vehicle_data.keys(),
        key=lambda d: vehicle_data[d]["waiting"] + vehicle_data[d]["approaching"],
        reverse=True
    )
    max_traffic_dir = sorted_directions[0]

    # Handle transition timer (e.g., yellow light phase)
    if transition_timer["countdown"] > 0:
        transition_timer["countdown"] -= 1
        for dir, light in traffic_lights.items():
            light.set_color('yellow' if dir == transition_timer["direction"] else 'red')
        return

    # Update traffic lights based on traffic
    for dir, light in traffic_lights.items():
        if dir == max_traffic_dir:
            if light.light.get_facecolor() == (0, 0, 0, 1):  # If light is black (off)
                transition_timer["direction"] = dir
                transition_timer["countdown"] = 2  # Set yellow light duration
                light.set_color('yellow')
            else:
                light.set_color('green')
            vehicle_data[dir]["waiting"] = max(vehicle_data[dir]["waiting"] - 2, 0)
        else:
            light.set_color('red')

def update_far_lights():
    """Update far lights based on approaching vehicle counts."""
    for dir, light in far_lights.items():
        if vehicle_data[dir]["approaching"] > 10:
            light.set_color('green')
        else:
            light.set_color('yellow')

def add_vehicle(direction, is_emergency=False):
    """Add a vehicle to the simulation."""
    x, y = {
        "North": (5, 11),
        "East": (11, 5),
        "South": (5, -1),
        "West": (-1, 5)
    }[direction]
    if is_emergency:
        vehicle = patches.Circle((x, y), 0.2, facecolor='red', edgecolor='black')
        emergency_vehicles.append((vehicle, direction))
    else:
        vehicle = patches.Circle((x, y), 0.2, facecolor='blue', edgecolor='black')
        vehicles.append((vehicle, direction))
    ax_main.add_patch(vehicle)

def move_vehicles():
    """Move vehicles based on traffic light status."""
    for vehicle, direction in vehicles + emergency_vehicles:
        x, y = vehicle.center
        if direction == "North" and y > 5:
            vehicle.center = (x, y - 0.1)
        elif direction == "South" and y < 5:
            vehicle.center = (x, y + 0.1)
        elif direction == "East" and x > 5:
            vehicle.center = (x - 0.1, y)
        elif direction == "West" and x < 5:
            vehicle.center = (x + 0.1, y)
        if (x - 5) ** 2 + (y - 5) ** 2 < 1:  # Vehicle has passed the intersection
            if (vehicle, direction) in emergency_vehicles:
                emergency_vehicles.remove((vehicle, direction))
            else:
                vehicles.remove((vehicle, direction))
            vehicle.remove()

def update_graphs(i):
    """Update the graphs and charts."""
    # Traffic distribution by direction
    ax_traffic_dist.clear()
    ax_traffic_dist.bar(traffic_distribution.keys(), traffic_distribution.values(), color=['blue', 'green', 'red', 'purple'])
    ax_traffic_dist.set_title("Traffic Distribution by Direction")
    ax_traffic_dist.set_ylabel("Number of Vehicles")
    ax_traffic_dist.set_xticks(range(len(traffic_distribution)))  # Set ticks explicitly
    ax_traffic_dist.set_xticklabels(traffic_distribution.keys(), rotation=45)

    # Emergency vehicles pie chart
    ax_emergency.clear()
    emergency_total = len(emergency_vehicles)
    regular_total = len(vehicles)
    ax_emergency.pie([emergency_total, regular_total], labels=["Emergency", "Regular"], autopct='%1.1f%%', colors=['red', 'blue'], startangle=90)
    ax_emergency.set_title("Emergency vs Regular Vehicles")

    # Waiting time over time
    ax_waiting_time.clear()
    for dir in traffic_lights.keys():
        ax_waiting_time.plot(time_steps, waiting_times[dir], label=dir)
    ax_waiting_time.set_title("Waiting Time Over Time")
    ax_waiting_time.set_xlabel("Time Step")
    ax_waiting_time.set_ylabel("Waiting Time")
    ax_waiting_time.grid(True)
    ax_waiting_time.legend()

    # Traffic density over time
    ax_traffic_density.clear()
    ax_traffic_density.plot(time_steps, traffic_density, label="Traffic Density", color='orange')
    ax_traffic_density.set_title("Traffic Density Over Time")
    ax_traffic_density.set_xlabel("Time Step")
    ax_traffic_density.set_ylabel("Density")
    ax_traffic_density.grid(True)

    # Average waiting time by direction
    ax_avg_waiting_time.clear()
    avg_waiting = {dir: np.mean(waiting_times[dir]) if waiting_times[dir] else 0 for dir in traffic_lights.keys()}
    ax_avg_waiting_time.bar(avg_waiting.keys(), avg_waiting.values(), color=['blue', 'green', 'red', 'purple'])
    ax_avg_waiting_time.set_title("Average Waiting Time by Direction")
    ax_avg_waiting_time.set_ylabel("Average Waiting Time")
    ax_avg_waiting_time.set_xticks(range(len(avg_waiting)))  # Set ticks explicitly
    ax_avg_waiting_time.set_xticklabels(avg_waiting.keys(), rotation=45)

    # Traffic flow efficiency
    ax_traffic_flow.clear()
    ax_traffic_flow.plot(time_steps, traffic_flow_efficiency, label="Efficiency", color='green')
    ax_traffic_flow.set_title("Traffic Flow Efficiency")
    ax_traffic_flow.set_xlabel("Time Step")
    ax_traffic_flow.set_ylabel("Efficiency")
    ax_traffic_flow.grid(True)

def animate_traffic(i):
    """Animate the traffic simulation for each frame."""
    if i < len(data):
        row = data.iloc[i]
        direction, emergency = row['Direction'], row['Emergency Detected']
        vehicle_data[direction]["waiting"] = row['Waiting Vehicles']
        vehicle_data[direction]["approaching"] = row['Approaching Vehicles']

        # Update traffic distribution data
        traffic_distribution[direction] += 1

        # Add vehicles based on approaching count
        if vehicle_data[direction]["approaching"] > 0:
            add_vehicle(direction, is_emergency=(emergency == "Yes"))

        # Update waiting times
        for dir in traffic_lights.keys():
            waiting_times[dir].append(vehicle_data[dir]["waiting"])

        # Update time steps
        time_steps.append(i)

        # Update traffic density
        traffic_density.append(len(vehicles) + len(emergency_vehicles))

        # Update traffic flow efficiency (placeholder)
        traffic_flow_efficiency.append(np.random.random())  # Replace with actual efficiency data

        # Update traffic and far lights
        update_traffic_lights(direction, emergency)
        update_far_lights()

        # Move vehicles
        move_vehicles()

        # Clear the axis and redraw the roundabout
        ax_main.clear()
        ax_main.set_xlim(-1, 11)
        ax_main.set_ylim(-1, 11)
        ax_main.set_aspect('equal')
        ax_main.axis('off')
        ax_main.add_patch(roundabout)

        # Draw arrows and text for each direction
        directions = ["North", "East", "South", "West"]
        positions = {"North": (5, 9), "East": (9, 5), "South": (5, 1), "West": (1, 5)}
        arrows = {"North": (0, -3), "East": (-3, 0), "South": (0, 3), "West": (3, 0)}

        for dir in directions:
            ax_main.arrow(*positions[dir], *arrows[dir], head_width=0.5, color='blue', length_includes_head=True)
            waiting_countdown = max(vehicle_data[dir]["waiting"], 0)
            approaching_count = vehicle_data[dir]["approaching"]
            
            # Adjust text box positions for North and South
            if dir == "North":
                text_position = (positions[dir][0] - 2.5, positions[dir][1])  # Move text to the left
            elif dir == "South":
                text_position = (positions[dir][0] - 2.5, positions[dir][1])  # Move text to the left
            else:
                text_position = (positions[dir][0] - 0.5, positions[dir][1] + 0.5)  # Default position

            ax_main.text(
                *text_position,
                f"{dir}\nWaiting: {waiting_countdown}\nApproaching: {approaching_count}\nEmergency: {emergency}",
                fontsize=8, bbox=dict(facecolor='white', alpha=0.8, edgecolor='black')
            )

        # Redraw traffic and far lights
        for light in traffic_lights.values():
            ax_main.add_patch(light.light)
        for light in far_lights.values():
            ax_main.add_patch(light.light)

        # Update graphs
        update_graphs(i)

# Create and display the animation
ani_traffic = animation.FuncAnimation(fig, animate_traffic, frames=len(data), interval=1000, repeat=False)

# Adjust layout to prevent overlapping
plt.subplots_adjust(left=0.05, right=0.95, top=0.95, bottom=0.05, hspace=0.8, wspace=0.8)
plt.show()
