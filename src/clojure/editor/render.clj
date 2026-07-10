(ns clojure.editor.render
  (:require [clojure.ctx :as ctx]
            [clojure.set-ctx :as set-ctx]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn render [{:keys [ctx/stage]
                     :as ctx}]
  (let [ctx (ctx/clear ctx)
        ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act stage)
    (stage/draw stage)
    (:stage/ctx stage)))
