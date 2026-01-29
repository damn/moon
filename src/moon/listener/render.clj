(ns moon.listener.render)

; == 'pipeline' ?
(defn do! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))
