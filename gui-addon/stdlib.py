import sys
def get(x, y):
    try:
        return x[y]
    except IndexError:
        return None
    except KeyError:
        return None
def has(x, y):
    if y == None:
        return False
    if x == None:
        return False
    try:
        x[y]
    except KeyError:
        return False
    except TypeError:
        for cmd in x:
            if cmd == y:
                return True
        return False
    return True
def lower(x):
    return x.lower()
def upper(x):
    return x.upper()
def strip(x):
    return x.strip()
def replace(x, y, z):
    return x.replace(y, z)
def charAt(x, y):
    return x[y]
def change(x, y, z):
    x[y] = z
    return x
def add(x, y):
    x.append(y)
    return x
def remove(x, y):
    x.remove(y)
    return x
def addKey(x, y, z):
    x[y] = z
    return x
def substring(x, y, z):
    if (z == False):
        return x[y:];
    else:
        return x[y:z];
def print_str(x):
    sys.stdout.write(str(x))