# Heading 1
Heading 1 other style
===

## Heading 2
Heading 2 other style
---

### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6

---

***

### Text formating

##### Just text

Paragraph text
No difference if it is in the same line or different lines

This text is with paragraph with two spaces  
check this out. And this line is with \
backslash

So this is text with the next line

Some text and then *Italic text,*. Looks good.
_Italic text again_

Some text and now **Bold text**, then again
__bold text with other style__

Here is some ~~mistaken text~~. Also looks fine

##### Blocks of code

 `Inline Code` with text after it

 ```
# Code block no language formatting
class Circle:
    pi = 3.14

    def __init__(self, radius):
        self.radius = radius
 ```
Here is one more block of code no formatting

    # Code block
    class Circle:
        pi = 3.14

        def __init__(self, radius):
            self.radius = radius

This is code of python:
```py
# Code block
class Circle:
    pi = 3.14

    def __init__(self, radius):
        self.radius = radius
```

Or code with bash
```bash
sudo snap install $PACKAGE
```

---

### Links formating

[this is a link](https://github.com/lxgolovin) and some text

[this is a link other style][1] and some text

![Screenshots](http://i.imgur.com/f1N9ZbD.jpg) and text

![Other screenshots][2] with some text

[1]: https://github.com/lxgolovin
[2]: http://i.imgur.com/f1N9ZbD.jpg

---

### Blocks and tables and structures

> Quote block for notes for example

##### A List

* Item 1
some text

* Item 2 with more text
* Item 3

##### A List same type

- Item 1
with text

- Item 2 with some text
- Item 3

##### A List

1. Item 1
some text

2. Item 2 with more text
3. Item 3

##### A nested List

* List item one
* List item two
    * A nested item

1. Number list item one		
	1.1. A nested item
2. Number list item two
3. Number list item three

##### Tasks
- [ ] a task list item
some text on other line
- [ ] list syntax required
- [ ] normal **formatting**
- [ ] incomplete
- [x] completed

##### Tasks again, but with one space
- [ ] a task list item
some text on other line


- [ ] incomplete
- [x] completed

##### Tasks again, but with space in all lines
- [ ] a task list item
some text on other line

- [ ] incomplete
- [x] completed

---

Table

| Left-Aligned  | Center Aligned  | Right Aligned |
| :------------ |:---------------:| -----:|
| col 3 is      | some wordy text | $1600 |
| col 2 is      | centered        |   $12 |
| zebra stripes | are neat        |    $1 |

-------------------------------------------------

### Good Links

* [commonmark page](https://commonmark.org) - nice tutorial for everybody
