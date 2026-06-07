(ns editor.app.render
  (:require [gdx.screen :as screen-utils]
            [com.badlogic.gdx.scenes.scene2d.stage.set-ctx :refer [set-ctx!]]
            [com.badlogic.gdx.scenes.scene2d.stage.act :refer [act!]]
            [com.badlogic.gdx.scenes.scene2d.stage.draw :refer [draw!]]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! 0 0 0 0)
    (set-ctx! stage ctx)
    (act! stage)
    (draw! stage)
    (:stage/ctx stage)))
