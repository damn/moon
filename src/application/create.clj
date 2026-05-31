(ns application.create)

(defn do! [app stepfns]
  (reduce (fn [ctx f]
            (f ctx))
          {:ctx/app app}
          stepfns))
