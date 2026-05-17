(ns game.render.if-not-paused)

(defn step
  [{:keys [ctx/paused?]
    :as ctx}
   fns]
  (if paused?
    ctx
    (reduce (fn [ctx f]
              (f ctx))
            ctx
            fns)))
