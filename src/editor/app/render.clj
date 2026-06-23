(ns editor.app.render
  (:require [scene2d.stage.set-ctx :refer [set-ctx!]]
            [scene2d.stage.act :refer [act!]]
            [scene2d.stage.draw :refer [draw!]]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx! stage ctx)
    (act! stage)
    (draw! stage)
    (:stage/ctx stage)))
