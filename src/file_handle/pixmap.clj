(ns file-handle.pixmap
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f [file-handle]
  (pixmap/create-from-file file-handle))
