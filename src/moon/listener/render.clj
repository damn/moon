(ns moon.listener.render)

(defn do! [ctx render-fns]
  (reduce (fn [ctx f]
            (f ctx))
          ctx
          render-fns))
