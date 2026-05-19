(ns com.badlogic.gdx.utils.disposable
  (:require [gdl.utils.disposable :as disposable])
  (:import (com.badlogic.gdx.utils Disposable)))

(extend-type Disposable
  disposable/Disposable
  (dispose! [this]
    (.dispose this)))
