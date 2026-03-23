(ns moon.listener.render) ; TODO this is pure and finished

(defn do! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))

; => core utils ( no dependency ns)

; what are the new core constructs (txs/draws)?
