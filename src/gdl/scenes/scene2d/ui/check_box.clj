(ns gdl.scenes.scene2d.ui.check-box
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn checked? [& args]
  (apply check-box/isChecked args))

(defn new [& args]
  (apply check-box/new args))

(defn set-checked! [& args]
  (apply check-box/setChecked args))
