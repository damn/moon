(ns clojure.gdx.utils.disposable
  (:require [com.badlogic.gdx.utils.disposable :as disposable]))

(defn dispose! [resource]
  (disposable/dispose resource))
