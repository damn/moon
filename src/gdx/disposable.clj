(ns gdx.disposable
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose! [resource]
  (Disposable/.dispose resource))
