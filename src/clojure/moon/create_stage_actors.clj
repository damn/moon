(ns clojure.moon.create-stage-actors
  (:require [clojure.moon.action-bar-create :refer [action-bar-create]]
            [clojure.moon.hp-mana-bar-create :refer [hp-mana-bar-create]]
            [clojure.moon.inventory-window-create :refer [inventory-window-create]]
            [clojure.moon.player-message-actor-create :refer [player-message-actor-create]]
            [clojure.moon.player-state-draw-create :refer [player-state-draw-create]]
            [clojure.moon.stage-dev-menu-create :refer [stage-dev-menu-create]]
            [clojure.moon.stage-info-window-create :refer [stage-info-window-create]]
            [clojure.moon.windows-create :refer [windows-create]]
            [gdl.scenes.scene2d.stage :as stage]))

(defn f [ctx]
  (doseq [[actor-fn & params] [[action-bar-create]
                                [stage-dev-menu-create]
                                [hp-mana-bar-create]
                                [windows-create [stage-info-window-create
                                                 inventory-window-create]]
                                [player-state-draw-create]
                                [player-message-actor-create]]]
    (stage/add-actor! (:ctx/stage ctx) (apply actor-fn ctx params)))
  ctx)
