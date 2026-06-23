find src -name '*.clj' |
while read f; do
    n=$(grep -c '^(' "$f")
    if [ "$n" -gt 2 ]; then
        echo "$f $n"
    fi
done | sort -n
