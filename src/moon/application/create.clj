(ns moon.application.create)

(defn do!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {}
          create-fns))
