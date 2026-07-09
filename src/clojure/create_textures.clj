(ns clojure.create-textures
  (:require [clojure.texture :as texture]
            [clojure.string :as str]
            [clojure.files :as files]
            [clojure.recursively-search :as recursively-search]))

; TODO this does 2 things
; * first generate just the 'paths' of my textures
; and make path - file-handle map
; next generate the textures
; path -> texture fn
; 1. step all the paths - anyway for deployment ....
; or shouldn't all things ocnnect its own textures? and dispose?
(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (files/internal files folder) extensions))]
             [path (texture/new (files/internal files path))])))
