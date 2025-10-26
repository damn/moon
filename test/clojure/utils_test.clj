#_(ns moon.utils-test)

#_(defn order-contains? [order k]
    ((apply hash-set (keys order)) k))

#_(deftest test-order
    (is
     (= (define-order [:a :b :c]) {:a 0 :b 1 :c 2}))
    (is
     (order-contains? (define-order [:a :b :c]) :a))
    (is
     (not
      (order-contains? (define-order [:a :b :c]) 2)))
    (is
     (=
      (sort-by-order [:c :b :a :b] identity (define-order [:a :b :c]))
      '(:a :b :b :c)))
    (is
     (=
      (sort-by-order [:b :c :null :null :a] identity (define-order [:c :b :a :null]))
      '(:c :b :a :null :null))))
