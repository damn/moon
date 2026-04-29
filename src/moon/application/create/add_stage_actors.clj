(ns moon.application.create.add-stage-actors
  (:require moon.application.create.add-stage-actors.dev-menu
            moon.application.create.add-stage-actors.action-bar
            moon.application.create.add-stage-actors.hp-mana-bar
            moon.application.create.add-stage-actors.windows
            moon.application.create.add-stage-actors.player-state-draw
            moon.application.create.add-stage-actors.player-message
            [clojure.gdx.scene2d.stage :as stage]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}]
  (doseq [actor [(moon.application.create.add-stage-actors.dev-menu/create ctx)
                 (moon.application.create.add-stage-actors.action-bar/create)
                 (moon.application.create.add-stage-actors.hp-mana-bar/create ctx)
                 (moon.application.create.add-stage-actors.windows/create ctx)
                 (moon.application.create.add-stage-actors.player-state-draw/create)
                 (moon.application.create.add-stage-actors.player-message/create)]]
    (stage/add-actor! stage actor))
  ctx)
