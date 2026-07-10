(ns clojure.moon.create-init-tooltip
  (:require [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn f [ctx]
  (tooltip-manager/setInitialTime (tooltip-manager/getInstance) 0)
  (colors/put "PRETTY_NAME" (color/new [0.84 0.8 0.52 1]))
  ctx)
