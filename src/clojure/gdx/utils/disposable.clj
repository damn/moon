(ns clojure.gdx.utils.disposable
  (:require [clojure.utils.disposable :as disposable])
  (:import (com.badlogic.gdx.utils Disposable)))

(extend-type Disposable
  disposable/Disposable
  (dispose! [this]
    (.dispose this)))
