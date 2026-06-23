find src -name '*.clj' |
while read f; do
    n=$(grep -c '^(' "$f")
    if [ "$n" -gt 2 ]; then
        printf "%5d %s\n" "$n" "$f"
    fi
done | sort -n | awk '
{ print; c++ }
END { print "\nFiles with >2 top-level forms:", c }
'
