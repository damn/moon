(ns create.add-stage-actors
  (:require [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [stage.dev-menu :as dev-menu]
            [stage.hp-mana-bar :as hp-mana-bar]
            [stage.player-state-draw :as player-state-draw]
            [stage.player-message-actor :as player-message-actor]
            [stage.info-window :as info-window]
            [stage.inventory-window :as inventory-window]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}]
  (doseq [actor [(dev-menu/create ctx)
                 (action-bar/create)
                 (hp-mana-bar/create ctx)
                 (group/create
                  {:group/actors [(info-window/create ctx)
                                  (inventory-window/create ctx)]
                   :actor/name "moon.ui.windows"})
                 (player-state-draw/create ctx)
                 (player-message-actor/create ctx)]]
    (stage/add-actor! stage actor))
  ctx)
