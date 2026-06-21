(ns clojure.pixmap.dispose
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn f! [^Pixmap pixmap]
  (.dispose pixmap))
