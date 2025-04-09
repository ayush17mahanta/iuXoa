import pygame
import random
import sys

# Init
pygame.init()
font = pygame.font.SysFont("arial", 26, bold=True)
screen = pygame.display.set_mode((0, 0), pygame.FULLSCREEN)
WIDTH, HEIGHT = screen.get_width(), screen.get_height()
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("AI Dynamic Lane Divider")
clock = pygame.time.Clock()
road_bg = pygame.image.load("road.jpg")
road_bg = pygame.transform.scale(road_bg, (WIDTH, HEIGHT))


# Load cars
car_left = pygame.image.load("car.png")
car_right = pygame.image.load("car2.png")
car_left = pygame.transform.scale(car_left, (200, 240))  # Wider & Taller
car_right = pygame.transform.scale(car_right, (200, 240))




# Colors
GRAY = (180, 180, 180)
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
BUTTON_COLOR = (50, 150, 250)
TEXT_COLOR = (255, 255, 255)  # White

font = pygame.font.SysFont("arial", 22)

# Car setup
LEFT_CARS = []
RIGHT_CARS = []
class Car:
    def __init__(self, x, y, direction, base_image):
        self.rect = pygame.Rect(x, y, 200, 240)
        self.blink_timer = 0
        self.speed = random.uniform(1.5, 3.5)
        self.image = self.tint_image(base_image, direction)
        self.direction = direction  # 'up' or 'down'

        # 🆕 Lane-changing attributes
        self.target_x = self.rect.x
        self.lane_switch_timer = random.randint(100, 300)  # Frames until next possible lane change
        self.switching = False

    def tint_image(self, base, direction):
        tint = pygame.Surface((200, 240)).convert_alpha()
        color = (
            random.randint(100, 255),
            random.randint(100, 255),
            random.randint(100, 255)
        )
        tint.fill(color)
        base_copy = base.copy()
        base_copy.blit(tint, (0, 0), special_flags=pygame.BLEND_RGBA_MULT)

        if direction == "down":
            return pygame.transform.flip(base_copy, False, True)
        return base_copy

    def maybe_start_lane_change(self):
        if not self.switching and self.lane_switch_timer <= 0:
            offset = random.choice([-60, 60])  # Shift left or right
            new_x = self.rect.x + offset

            # Clamp target_x so it stays in bounds
            if 80 < new_x < WIDTH - 220:
                self.target_x = new_x
                self.blink_timer = 30  


            self.lane_switch_timer = random.randint(200, 500)  # Reset timer
        else:
            self.lane_switch_timer -= 1

    def update_lane_change(self):
        if self.switching:
            if abs(self.rect.x - self.target_x) < 2:
                self.rect.x = self.target_x
                self.switching = False
            elif self.rect.x < self.target_x:
                self.rect.x += 2
            else:
                self.rect.x -= 2

    def move(self):
        self.maybe_start_lane_change()

        if self.blink_timer > 0:
            self.blink_timer -= 1
            if self.blink_timer == 0:
                self.switching = True  # Begin actual lane switch after blink

        self.update_lane_change()

        if self.direction == "up":
            self.rect.y -= self.speed
            if self.rect.y < -240:
                self.rect.y = HEIGHT + random.randint(50, 200)
        else:
            self.rect.y += self.speed
            if self.rect.y > HEIGHT:
                self.rect.y = -random.randint(200, 300)


    def draw(self, surface):
        surface.blit(self.image, self.rect.topleft)




divider_x = WIDTH // 2
lane_width_left = 0
lane_width_right = 0

# Traffic presets
traffic_settings = {
    "normal": {"left": 10, "right": 10, "divider": WIDTH // 2},
    "morning": {"left": 15, "right": 6, "divider": WIDTH // 2 + 80},
    "evening": {"left": 6, "right": 15, "divider": WIDTH // 2 - 80}
}
condition = "normal"

# Buttons

buttons = {
    "normal": pygame.Rect(WIDTH//2 - 240, HEIGHT - 60, 150, 40),
    "morning": pygame.Rect(WIDTH//2 - 75, HEIGHT - 60, 150, 40),
    "evening": pygame.Rect(WIDTH//2 + 90, HEIGHT - 60, 150, 40)
}


def draw_buttons():
    for label, rect in buttons.items():
        pygame.draw.rect(screen, BUTTON_COLOR, rect, border_radius=8)
        txt = font.render(label.capitalize(), True, TEXT_COLOR)
        screen.blit(txt, (rect.x + (rect.width - txt.get_width()) // 2,
                          rect.y + (rect.height - txt.get_height()) // 2))

        


def reset_cars():
    LEFT_CARS.clear()
    RIGHT_CARS.clear()

    count = traffic_settings[condition]
    global divider_x
    divider_x = count["divider"]

   # LEFT (incoming)
    for _ in range(count["left"]):
        placed = False
        attempts = 0
        while not placed and attempts < 100:
            x = random.randint(80, divider_x - 220)
            y = random.randint(0, HEIGHT - 250)
            new_rect = pygame.Rect(x, y, 200, 240)
            if not any(new_rect.colliderect(c.rect) for c in LEFT_CARS):
                LEFT_CARS.append(Car(x, y, "up", car_left))
                placed = True
            attempts += 1

    # RIGHT (outgoing)
    for _ in range(count["right"]):
        placed = False
        attempts = 0
        while not placed and attempts < 100:
            x = random.randint(divider_x + 20, WIDTH - 220)
            y = random.randint(0, HEIGHT - 250)
            new_rect = pygame.Rect(x, y, 200, 240)
            if not any(new_rect.colliderect(c.rect) for c in RIGHT_CARS):
                RIGHT_CARS.append(Car(x, y, "down", car_right))
                placed = True
            attempts += 1



def move_cars():
    for car in LEFT_CARS + RIGHT_CARS:
        car.move()


def draw_scene():
    screen.blit(road_bg, (0, 0))

    # Draw black & yellow dashed divider
    dash_height = 40
    gap = 10
    y = 0
    toggle = True
    while y < HEIGHT - 80:
        color = BLACK if toggle else (255, 204, 0)
        pygame.draw.line(screen, color, (divider_x, y), (divider_x, y + dash_height), 14)
        y += dash_height + gap
        toggle = not toggle

    # Cars
    for car in LEFT_CARS + RIGHT_CARS:
        car.draw(screen)


    # Title
    title = font.render(f"Traffic Condition: {condition.upper()}", True, TEXT_COLOR)
    screen.blit(title, (WIDTH // 2 - title.get_width() // 2, 40))

    # Buttons
    draw_buttons()





def main():
    global condition
    reset_cars()

    while True:
        screen.fill(WHITE)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()

            elif event.type == pygame.MOUSEBUTTONDOWN:
                for label, rect in buttons.items():
                    if rect.collidepoint(event.pos):
                        condition = label
                        reset_cars()

        move_cars()
        draw_scene()

        pygame.display.flip()
        clock.tick(60)

if __name__ == "__main__":
    main()
