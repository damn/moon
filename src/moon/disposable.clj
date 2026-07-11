(ns moon.disposable
  (:require [com.badlogic.gdx.utils.disposable :as disposable]))

(defn dispose! [resource]
  (disposable/dispose resource))
