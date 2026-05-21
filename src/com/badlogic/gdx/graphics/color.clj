(ns com.badlogic.gdx.graphics.color
  (:require [clojure.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Color)))

(.bindRoot #'color/float-bits
           (fn float-bits [[r g b a]]
             (Color/toFloatBits (float r)
                                (float g)
                                (float b)
                                (float a))))

(.bindRoot #'color/create
           (fn [[r g b a]]
             (Color. r g b a)))
