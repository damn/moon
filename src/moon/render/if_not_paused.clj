(ns moon.render.if-not-paused)

(defn do!
  [{:keys [ctx/paused?]
    :as ctx}
   fns]
  (if paused?
    ctx
    (reduce (fn [ctx [f & params]]
              (apply f ctx params))
            ctx
            fns)))
