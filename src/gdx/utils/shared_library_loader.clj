(ns gdx.utils.shared-library-loader
  (:import (com.badlogic.gdx.utils SharedLibraryLoader)))

(defn os []
  SharedLibraryLoader/os)
