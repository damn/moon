(ns moon.listener.render)

(defn do! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))
