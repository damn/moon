(ns clojure.editor.create
  (:require [clojure.editor.ctx-from-app :as ctx-from-app]
            [clojure.editor.ctx-batch-step :as ctx-batch-step]
            [clojure.editor.ctx-skin-step :as ctx-skin-step]
            [clojure.editor.ctx-db-step :as ctx-db-step]
            [clojure.editor.stage :as stage]
            [clojure.editor.ctx-textures-step :as ctx-textures-step]))

(defn create [app]
  (-> app
      ctx-from-app/f
      ctx-batch-step/f
      ctx-skin-step/f
      ctx-db-step/f
      stage/f
      ctx-textures-step/f))
