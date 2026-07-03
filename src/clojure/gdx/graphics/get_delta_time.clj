(ns clojure.gdx.graphics.get-delta-time
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (Graphics/.getDeltaTime graphics))
