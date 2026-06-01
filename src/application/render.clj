(ns application.render)

(defn do! [ctx stepfns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          stepfns))
