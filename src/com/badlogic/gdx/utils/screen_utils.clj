(ns com.badlogic.gdx.utils.screen-utils
  (:require [clojure.app :as app])
  (:import (com.badlogic.gdx.utils ScreenUtils)))

(.bindRoot #'app/clear-screen!
           (fn [[r g b a]]
             (ScreenUtils/clear r g b a)))
