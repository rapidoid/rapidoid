git diff -U0 | grep '^[+-]' | grep -Ev '^(--- a/|\+\+\+ b/)' > diff.txt
