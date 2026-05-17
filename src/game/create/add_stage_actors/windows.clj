(ns game.create.add-stage-actors.windows)

(defn create [ctx actor-fns]
  {:type :ui/group
   :group/actors (for [f actor-fns]
                   (f ctx))
   :actor/name "moon.ui.windows"})
