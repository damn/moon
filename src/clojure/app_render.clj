(ns clojure.app-render
  (:require [clojure.stage :as stage]
            [clojure.set-ctx :as set-ctx]))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (set-ctx/f stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))
