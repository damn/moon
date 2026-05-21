(ns com.badlogic.gdx.graphics.colors
  (:require [clojure.app :as app]
            [clojure.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(.bindRoot #'app/put-colors!
           (fn [colors]
             (doseq [[name rgba] colors]
               (Colors/put name (color/create rgba)))))
