(ns clojure.editor.create
  (:require [clojure.editor.input :as input]
            [clojure.editor.audio :as audio]
            [clojure.editor.files :as files]
            [clojure.editor.graphics :as graphics]
            [clojure.editor.batch :as batch]
            [clojure.editor.skin :as skin]
            [clojure.editor.db :as db]
            [clojure.editor.stage :as stage]
            [clojure.editor.textures :as textures]
            [clojure.editor.create-widget-register-methods]))

(defn create [^com.badlogic.gdx.Application app]
  (-> {:ctx/app app}
      input/f
      audio/f
      files/f
      graphics/f
      batch/f
      skin/f
      db/f
      stage/f
      textures/f))
