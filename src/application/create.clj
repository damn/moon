(ns application.create)

(defn do! [app stepfns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          {:ctx/app app}
          stepfns))
