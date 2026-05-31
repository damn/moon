(ns application.render)

(defn do! [ctx stepfns]
  (reduce (fn [ctx f]
            (f ctx))
          ctx
          stepfns))
