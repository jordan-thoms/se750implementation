"""
Implementation of PDStore in Python

More details to come.

Author       : Danver Braganza
Date started : 2011-08-29
"""

import sys, os

import mmap

import uuid

def show_usage():
    print('''Do not run this file: please execute pydstore instead.
''')

if __name__ = '__main__':
    show_usage()
    sys.exit()

class Transaction(object):
    def __init__(self, file_store, transaction_id=None):
        pass

    def add_link(self, source, rel_type, dest):
        '''
        Adds a link to the store backing this transaction.
        
        The link will not become visible to other transactions unless
        this transaction is committed.
        '''

    def remove_link(self, source, rel_type, dest):
        '''
        Removes a link from the store backing this transaction.
        
        This change will not become visible to other transactions unless
        this transaction is committed.
        '''

    
    def set_link(self, source, rel_type, dest):
        '''
        Sets a link to the store backing this transaction by first calling:
        1. get_link to find the current (or last) dest,
        2. remove_link to remove it
        3. add_link to link the new dest.

        Use add_link if removal of the old link does not matter, or is
        implicit.
        
        The link will not become visible to other transactions unless
        this transaction is committed.
        '''

    
    def get_links(self, rel_type, source):
        '''
        Returns an iterator of all dests related by rel_type from source.       
        '''
        
    def get_link(self, rel_type, source):
        '''
        Returns the last dest related by rel_type from source
        ''' 
    
    def commit(self):
        '''
        Commits all changes.  This transaction is no longer usable after this
        call.
        ''' 

class FileStore(object):
    def __init__(self, filename):
        storage_file = open(filename, 'wb+')

        # Before we can map, we put in a PDSTORE banner
        # because it looks nice and because Windows won't let us
        # mmap an empty file
        if not os.path.getsize(storage_file.name):
            storage_file.write("PDSTORE")
            storage_file.flush()
        
        self.filemap = mmap.mmap(storage_file.fileno,
                                 0,
                                 access=mmap.ACCESS_COPY #Great for many-reading
                                                         #and batched-writing
                                 )
        self.root_page = Page(self.filemap, 0)

    def begin(self):
        return Transaction(self)
    


class Page(object):
    '''
    A Page represents a 4KiB memory block.

    Try and use pages as little as possible, please.
    ''' 
    pass
    
        
        
