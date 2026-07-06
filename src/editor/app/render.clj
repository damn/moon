(ns editor.app.render
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [scene2d.stage :refer [set-ctx!]]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))
