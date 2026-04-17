(ns clojure.gdx.tooltip-manager
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn configure!
  "Requires `Gdx.app` to be initialized (use after application-listener/create)."
  [{:keys [initial-time]}]
  (tooltip-manager/set-initial-time! initial-time))
