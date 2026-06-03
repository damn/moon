(ns editor.app.render
  (:require [clojure.gdx.utils.screen-utils :as screen-utils]
            [clojure.gdx.scene2d.stage.set-ctx :refer [set-ctx!]]
            [gdx.stage :as stage]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}
   _params]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! 0 0 0 0)
    (set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))
