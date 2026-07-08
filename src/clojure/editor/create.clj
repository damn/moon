(ns clojure.editor.create
  (:require [clojure.editor.create.ctx-from-app :as ctx-from-app]
            [clojure.editor.create.ctx-batch-step :as ctx-batch-step]
            [clojure.editor.create.ctx-skin-step :as ctx-skin-step]
            [clojure.editor.create.ctx-db-step :as ctx-db-step]
            [clojure.editor.create.stage :as stage]
            [clojure.editor.create.ctx-textures-step :as ctx-textures-step]))

(defn create [app]
  (-> app
      ctx-from-app/f
      ctx-batch-step/f
      ctx-skin-step/f
      ctx-db-step/f
      stage/f
      ctx-textures-step/f))
