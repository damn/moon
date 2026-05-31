(ns stage.windows
  (:require [gdx.scenes.scene2d.group :as group]
            [stage.info-window :as info-window]
            [stage.inventory-window :as inventory-window]))

(defn create [ctx]
  (group/create
   {:group/actors [(info-window/create ctx)
                   (inventory-window/create ctx)]
    :actor/name "moon.ui.windows"}))
