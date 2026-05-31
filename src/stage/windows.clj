(ns stage.windows
  (:require [gdx.scenes.scene2d.group :as group]))

(defn create [ctx actor-fns]
  (group/create
   {:group/actors (for [f actor-fns]
                    (f ctx))
    :actor/name "moon.ui.windows"}))
