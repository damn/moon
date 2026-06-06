(ns gdx.to-clj)

(defprotocol Clojurize
  (->clj [_]))

(extend-type com.badlogic.gdx.math.Vector2
  Clojurize
  (->clj [v2]
    [(.x v2)
     (.y v2)]))

(extend-type com.badlogic.gdx.math.Vector3
  Clojurize
  (->clj [v3]
    [(.x v3)
     (.y v3)
     (.z v3)]))

(extend-type com.badlogic.gdx.maps.MapProperties
  Clojurize
  (->clj [props]
    (zipmap (.getKeys props)
            (.getValues props))))
