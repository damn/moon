(ns gdl.tooltip-manager
  (:require [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn get-instance [& args]
  (apply tooltip-manager/getInstance args))

(defn set-initial-time! [& args]
  (apply tooltip-manager/setInitialTime args))
