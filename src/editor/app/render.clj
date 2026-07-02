(ns editor.app.render
  (:require [clojure.gdx.stage.act :as act]
            [clojure.gdx.stage.draw :as draw]
            [clojure.gdx.stage.set-ctx :as set-ctx]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (act/f stage)
    (draw/f stage)
    (:stage/ctx stage)))
