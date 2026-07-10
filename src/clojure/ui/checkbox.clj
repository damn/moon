(ns clojure.ui.checkbox
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]))

(defn new [& args]
  (apply check-box/new args))

(defn set-checked! [& args]
  (apply check-box/setChecked args))

(defn checked? [& args]
  (apply check-box/isChecked args))
