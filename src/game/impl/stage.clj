(ns game.impl.stage
  (:require [com.badlogic.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.viewport :as viewport]))

(defn create
  [{:keys [ctx/batch]}]
  (ctx-stage/create (fit-viewport/create 1440 900)
                    batch))

(extend-type com.badlogic.gdx.scenes.scene2d.CtxStage
  stage/Stage
  (set-ctx! [stage ctx]
    (ctx-stage/set-ctx! stage ctx))

  (add-actor! [stage actor]
    (ctx-stage/add-actor! stage (actor/create actor)))

  (act! [stage]
    (ctx-stage/act! stage))

  (draw! [stage]
    (ctx-stage/draw! stage))

  (find-actor [stage name]
    (-> stage
        ctx-stage/root
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (ctx-stage/hit stage
                   (-> stage :stage/viewport (viewport/unproject position))
                   true)))
