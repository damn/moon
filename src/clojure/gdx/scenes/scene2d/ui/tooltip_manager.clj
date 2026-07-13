(ns clojure.gdx.scenes.scene2d.ui.tooltip-manager
  (:require [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn get-instance []
  (tooltip-manager/getInstance))

(defn set-initial-time! [tooltip-manager initial-time]
  (tooltip-manager/setInitialTime tooltip-manager initial-time))
