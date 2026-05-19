(ns game.impl.stage
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn create
  [{:keys [ctx/batch]}]
  (gdx/stage (gdx/fit-viewport 1440 900) batch))
